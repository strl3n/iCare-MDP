# iCare — Mental Health & Suicide Prevention App

Android client for **iCare**, a mobile mental health tracking application
built as a final project for the Mobile Device Programming course.

## Background

Suicide remains one of the leading causes of death worldwide, and
adolescents and young adults are among the groups most at risk. According
to the World Health Organization, suicide is the third leading cause of
death among 15–29-year-olds globally, and many cases go unnoticed until it
is too late — often because early warning signs are missed, mental health
support is hard to access, or there is stigma around asking for help.

This growing concern, particularly among teenagers and young adults, is the
main motivation behind the development of **iCare**. Recognizing how
widespread and serious this issue has become, we wanted to build something
that could offer a small but meaningful contribution: a simple, accessible
tool that encourages users to check in with their own emotional wellbeing
regularly, track their mood over time, and access supportive, positive
content whenever they need it.

iCare is developed in direct alignment with **UN Sustainable Development
Goal 3.2.4**, which specifically targets the reduction of the global
suicide mortality rate. By making mental health tracking approachable and
non-intimidating, we hope this app can help users — especially students and
young people — build healthier emotional awareness habits, recognize when
they might need support, and feel less alone in what they are going through.

If you or someone you know is struggling, please don't hesitate to reach
out to a trusted person, a mental health professional, or a local crisis
hotline.

## Features

- **Authentication** — register/login with email & password, plus Google
  Sign-In (Firebase Authentication).
- **Mood Tracker** — log a daily mood level (1–10) with an optional personal
  note.
- **Mood History & Statistics** — view past mood entries, weekly average,
  highest/lowest mood, and total entries logged.
- **Positive Articles & Activities** — a feed combining curated local
  content with activity suggestions from a third-party API, designed to
  support emotional wellbeing rather than expose users to potentially
  distressing news content. Includes a live search to filter articles.
- **Daily Motivational Quote** — a fresh, uplifting quote fetched from the
  backend on every app open.
- **Emergency Contacts** — quick-dial access to hotlines for users who need
  immediate support.
- **Offline-first Mood Saving** — mood entries are saved locally even if the
  network request to the backend fails, so no data is lost.

## Tech Stack

- **Kotlin**
- **MVVM Architecture** with the Repository pattern
- **Room** — local database (offline-first storage for users & moods)
- **Retrofit + OkHttp** — networking, communicating with the
  [iCare Backend](https://github.com/strl3n/iCare-Backend) (Node.js +
  MongoDB, deployed on Vercel)
- **Navigation Component** (with Safe Args) — fragment navigation
- **LiveData + ViewModel** — lifecycle-aware state management
- **View Binding** — type-safe view access
- **Coroutines** — asynchronous operations
- **Firebase Authentication** + **Google Sign-In** — third-party login
- **Material Components** — UI

## Architecture Overview

```
Fragment (UI)
    -> ViewModel (LiveData, coroutine scope)
        -> Repository
            -> DAO (Room, local data)
            -> ApiService (Retrofit, remote data)
```

- **Fragments** only observe `LiveData` exposed by `ViewModel`s and never
  access Room or Retrofit directly.
- **Repositories** (`AuthRepository`, `MoodRepository`, `ArticleRepository`)
  decide where data comes from — remote API, local database, or both — and
  handle syncing between them (e.g. saving mood data locally even when the
  network call fails).
- A detailed breakdown of the repository pattern, LiveData locations, and
  non-CRUD features is available in
  [`Penjelasan_Arsitektur_iCare.txt`](./Penjelasan_Arsitektur_iCare.txt)
  (in Indonesian).

## Project Structure

```
app/src/main/java/com/istts/finalproject/
├── data/
│   ├── local/
│   │   ├── dao/            # Room DAOs (UserDao, MoodDao)
│   │   ├── entity/         # Room entities (UserEntity, MoodEntity)
│   │   └── AppDatabase.kt
│   └── remote/
│       ├── model/          # Request/response models (Retrofit + third-party APIs)
│       ├── repository/     # AuthRepository, MoodRepository, ArticleRepository
│       ├── ApiService.kt   # iCare backend endpoints
│       ├── RetrofitClient.kt
│       └── BoredRetrofitClient.kt / BoredApiService.kt  # 3rd-party activity API
├── ui/
│   ├── auth/                # Login, Register
│   ├── dashboard/
│   ├── mood/                 # Mood Tracker, Mood History
│   ├── article/               # Article List, Article Detail
│   ├── profile/               # Profile, Edit Profile
│   └── emergency/             # Emergency Contacts
├── viewmodel/                  # AuthViewModel, MoodViewModel, ArticleViewModel
└── utils/                        # SessionManager, Resource, DateConverter, etc.
```

## Backend

This app communicates with a separate backend service:
[iCare-Backend](https://github.com/strl3n/iCare-Backend) — a Node.js +
Express + MongoDB REST API deployed on Vercel, handling authentication,
mood data persistence, and quote delivery.

## Setup

1. Clone this repository and open it in Android Studio.
2. Add your own `google-services.json` file (from your Firebase project)
   into the `app/` directory — this file is intentionally excluded from
   version control since it contains project-specific keys.
3. Update `RetrofitClient.kt` with your backend's base URL if you're running
   your own instance of the backend.
4. Build and run on a device or emulator (minSdk 24).

## Third-Party APIs Used

- **RapidAPI (Quotes)** — daily motivational quotes, called from the
  backend.
- **BoredAPI** (`boredapi.com`) — free, public API providing activity
  suggestions, used to supplement the article feed with positive,
  mood-boosting content.

## License

This project was developed for academic purposes as part of a Mobile
Device Programming course final project.
