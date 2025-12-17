MTG Collection Manager 🃏
A native Android application for Magic: The Gathering collectors.

Project Overview
the MTG Collection Manager to solve a common problem for collectors: organizing thousands of cards while keeping track of their fluctuating market values. The goal was to create a seamless experience where users can manage their digital binders offline, but have everything safely backed up to the cloud the moment they go online.

This project was a deep dive into modern Android development, focusing specifically on Clean Architecture and robust Offline-First data synchronization.

📱 What It Does
The app is mainly designed to be the central hub for a player's collection, making it easy to manage and track your collection of magic the gathering cards:

Organize Your Way: Users can create unlimited collections (like "Commander Decks" or "Trade Binders") and use custom color-coded labels to sort cards.

Price Tracking: It pulls real-time pricing data from major marketplaces like TCGPlayer and CardMarket, so users always know the total value of their collection.

Cloud Sync: I integrated Firebase so that data is accessible across devices.

Offline First: The app works perfectly without internet. You can view, edit, and manage cards on the subway or at a convention, and it syncs automatically when connection is restored.

🛠️ Tech Stack
I chose a modern stack to ensure the app is scalable and maintainable:

Language: Kotlin (100%)

Architecture: MVI with Clean Architecture

UI: XML with ViewBinding & Material Design 3

Async: Coroutines & Flow for reactive data handling

Local Data: Room Database (v3)

Remote Data: Firebase Firestore & Auth

Network: Retrofit + OkHttp (communicating with Scryfall API)

DI: Hilt

🏗️ Architecture & Major Systems
1. The "Offline-First" Sync Engine
   The most complex part of this project was ensuring data integrity between the local device and the cloud. I implemented a Repository Pattern that acts as a single source of truth:

Read Strategy: The app prioritizes the local Room database for instant UI loading, then fetches fresh data from Firestore in the background to update the cache.

Write Strategy: When a user modifies a collection, it updates the local database immediately (for a snappy UX) and queues a worker to sync the change to Firestore.

2. Scryfall API Integration
   I integrated the Scryfall API to power the search and pricing features. This required handling complex JSON responses with Kotlin Serialization to map nested data (like different card printings and mana costs) into clean UI models.