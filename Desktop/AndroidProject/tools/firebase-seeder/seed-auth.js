#!/usr/bin/env node
/*
 * Firebase Auth seeding utility for MentorConnect.
 * Requires the same environment variables as seed.js:
 *   GOOGLE_APPLICATION_CREDENTIALS
 *   FIREBASE_DATABASE_URL (only needed so firebase-admin can init database)
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

function pickDefined(source, keys) {
  return keys.reduce((acc, key) => {
    if (source[key] !== undefined && source[key] !== null && source[key] !== '') {
      acc[key] = source[key];
    }
    return acc;
  }, {});
}

async function seedAuthUsers(users = []) {
  if (!users.length) {
    console.log('No auth users defined in seed-data.json');
    return;
  }

  for (const user of users) {
    const { uid, email, password } = user;
    if (!uid || !email || !password) {
      console.warn('Skipping auth user because uid/email/password is missing', user);
      continue;
    }

    try {
      if (shouldReset) {
        try {
          await admin.auth().deleteUser(uid);
          console.log(`Deleted existing auth user ${uid}`);
        } catch (err) {
          if (err.code !== 'auth/user-not-found') {
            throw err;
          }
          console.log(`Auth user ${uid} did not exist; nothing to delete`);
        }
      }

      let existing = null;
      try {
        existing = await admin.auth().getUser(uid);
      } catch (err) {
        if (err.code !== 'auth/user-not-found') {
          throw err;
        }
      }

      if (!existing) {
        await admin.auth().createUser({
          uid,
          email,
          password,
          ...pickDefined(user, ['displayName', 'phoneNumber', 'photoURL', 'disabled'])
        });
        console.log(`Created auth user ${uid}`);
      } else {
        await admin.auth().updateUser(uid, {
          email,
          password,
          ...pickDefined(user, ['displayName', 'phoneNumber', 'photoURL', 'disabled'])
        });
        console.log(`Updated auth user ${uid}`);
      }
    } catch (err) {
      console.error(`Failed to process auth user ${user.uid}:`, err.message);
      throw err;
    }
  }

  console.log(`Auth seeding complete for ${users.length} user(s)`);
}

async function main() {
  const raw = fs.readFileSync(dataPath, 'utf-8');
  const seedData = JSON.parse(raw);
  await seedAuthUsers(seedData.auth?.users || []);
}

main()
  .then(() => {
    console.log('Auth seeding finished ✅');
    process.exit(0);
  })
  .catch((err) => {
    console.error('Auth seeding failed:', err);
    process.exit(1);
  });
