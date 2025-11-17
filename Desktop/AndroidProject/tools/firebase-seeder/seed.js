#!/usr/bin/env node
/*
 * Firebase seeding utility for MentorConnect.
 * Usage:
 *   GOOGLE_APPLICATION_CREDENTIALS=serviceAccount.json \
 *   FIREBASE_DATABASE_URL=https://<project>-default-rtdb.<region>.firebasedatabase.app \
 *   node seed.js [--reset]
 */

const fs = require('fs');
const path = require('path');
const admin = require('firebase-admin');

const RESET_FLAGS = new Set(['--reset', '-r']);
const shouldReset = process.argv.slice(2).some((arg) => RESET_FLAGS.has(arg));
const dataPath = path.join(__dirname, 'seed-data.json');

function ensureEnv(varName) {
  if (!process.env[varName]) {
    console.error(`Missing required environment variable: ${varName}`);
    process.exit(1);
  }
}

ensureEnv('GOOGLE_APPLICATION_CREDENTIALS');
ensureEnv('FIREBASE_DATABASE_URL');

if (!fs.existsSync(dataPath)) {
  console.error(`Seed data file not found at ${dataPath}`);
  process.exit(1);
}

admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  databaseURL: process.env.FIREBASE_DATABASE_URL
});

const firestore = admin.firestore();
const realtimeDb = admin.database();

async function seedCollection(collectionName, docs = []) {
  if (!docs.length) return;

  for (const doc of docs) {
    const { id, ...data } = doc;
    if (!id) {
      console.warn(`Skipping document without id in collection ${collectionName}`);
      continue;
    }

    const normalizedData = { ...data };
    if (normalizedData.status && typeof normalizedData.status === 'string') {
      normalizedData.status = normalizedData.status.toUpperCase();
    }

    const docRef = firestore.collection(collectionName).doc(id);

    if (shouldReset) {
      try {
        await docRef.delete();
      } catch (err) {
        if (err.code !== 5) { // ignore not-found
          throw err;
        }
      }
    }

    await docRef.set(normalizedData, { merge: true });
  }

  console.log(`Seeded ${docs.length} document(s) into ${collectionName}`);
}

async function seedMessages(messages = []) {
  for (const convo of messages) {
    const { conversationId, messages: payload = [] } = convo;
    if (!conversationId || !payload.length) continue;

    for (const msg of payload) {
      if (!msg.id) continue;
      const messageRef = realtimeDb
        .ref(`messages/${conversationId}/${msg.id}`);

      if (shouldReset) {
        await messageRef.remove().catch(() => undefined);
      }

      await messageRef.set(msg);
    }
  }

  console.log(`Seeded ${messages.length} conversation(s) into Realtime Database messages node`);
}

async function seedChatThreads(entries = []) {
  for (const entry of entries) {
    const { userId, threads = [] } = entry;
    if (!userId || !threads.length) continue;

    for (const thread of threads) {
      if (!thread.conversationId) continue;
      const threadRef = realtimeDb
        .ref(`chatThreads/${userId}/${thread.conversationId}`);

      if (shouldReset) {
        await threadRef.remove().catch(() => undefined);
      }

      await threadRef.set(thread);
    }
  }

  console.log(`Seeded chat thread metadata for ${entries.length} user(s)`);
}

async function seedFirestoreBlocks(data) {
  const {
    mentors = [],
    users = [],
    timeSlots = [],
    videoCallSessions = []
  } = data;

  await seedCollection('mentors', mentors);
  await seedCollection('users', users);
  await seedCollection('timeSlots', timeSlots);
  await seedCollection('videoCallSessions', videoCallSessions);
}

async function seedRealtimeBlocks(data) {
  const { messages = [], chatThreads = [] } = data;
  await seedMessages(messages);
  await seedChatThreads(chatThreads);
}

async function main() {
  const raw = fs.readFileSync(dataPath, 'utf-8');
  const seedData = JSON.parse(raw);

  console.log(`Starting Firebase seed${shouldReset ? ' (reset mode)' : ''}...`);

  await seedFirestoreBlocks(seedData.firestore || {});
  await seedRealtimeBlocks(seedData.realtime || {});

  console.log('Seed completed ✅');
  process.exit(0);
}

main().catch((err) => {
  console.error('Seeding failed:', err);
  process.exit(1);
});
