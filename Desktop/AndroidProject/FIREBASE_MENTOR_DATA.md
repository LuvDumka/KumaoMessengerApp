# Mentors collection setup

The app now reads mentor profiles from the Firestore collection `mentors`. Add one document per mentor with the following fields:

| Field        | Type             | Description                                                |
|--------------|------------------|------------------------------------------------------------|
| `name`       | string           | Mentor display name (required)                             |
| `expertise`  | string           | Primary focus area (e.g., "Android Development")           |
| `bio`        | string           | Short paragraph shown on the mentor card/detail page       |
| `experience` | string           | Experience label ("5+ years", "Senior PM", etc.)          |
| `hourlyRate` | number           | Price per hour (integer)                                   |
| `rating`     | number           | Optional average rating (defaults to 4.5 if omitted)       |
| `skills`     | array of strings | Used by the HelpBot filters; list relevant keywords        |
| `avatarUrl`  | string           | (Optional) HTTPS URL to a profile image                    |

A minimal document example:

```json
{
  "name": "Aarav Patel",
  "expertise": "Data Science",
  "bio": "Senior data scientist guiding graduates into analytics.",
  "experience": "8+ years",
  "hourlyRate": 70,
  "rating": 4.9,
  "skills": ["python", "machine learning", "sql"],
  "avatarUrl": "https://example.com/images/aarav.png"
}
```

> Tip: use the mentor's Firebase Auth UID as the document ID if you want to link bookings directly. The UI only shows mentors who exist in this collection, so add at least one document before testing.
