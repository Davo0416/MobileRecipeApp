# Mobile Recipe Android Application

## Overview

This project is a Android application that allows users to **discover, search, and explore recipes from around the world**. The application integrates with external recipe APIs to provide a large collection of meals, detailed cooking instructions, ingredient information, preparation times, video tutorials, and filtering options.

The app combines **intuitive navigation**, **interactive filtering**, and **rich recipe content** to create a user-friendly experience for anyone looking to discover and prepare new meals.

---

## Features

- **Recipe Search**
  - Search recipes by name or keyword
  - Instant filtering of displayed results

- **Advanced Recipe Filtering**
  - Filter recipes by country of origin
  - Filter recipes by category
  - Maximum preparation time filtering
  - Maximum ingredient count filtering
  - Clear all filters with a single button

- **Recipe Browsing**
  - Scrollable recipe list using RecyclerView
  - Recipe cards displaying:
    - Meal image
    - Country flag
    - Recipe category
    - Preparation time
    - Ingredient count

- **Detailed Recipe View**
  - Full recipe instructions
  - Ingredient list
  - Recipe image
  - Country and category indicators
  - Preparation time display
  - Embedded video tutorial support

- **Language Support**
  - Dynamic language switching
  - Localized user interface text

- **Responsive User Interface**
  - Material Design components
  - Optimized for different Android device sizes
  - Smooth navigation between screens

- **End-to-End Testing**
  - Espresso UI testing
  - Validation of filtering, searching, navigation, and recipe display functionality

---

## Technology Stack

**Frontend / Mobile Application:**

- Java
- Android SDK
- Android Studio
- XML Layouts
- Material Design Components

**Libraries & Frameworks:**

- RecyclerView
- Volley (API communication)
- Glide (image loading and caching)
- Material Components
- Espresso (UI testing)

**APIs:**

- TheMealDB API for recipe information
- Country Flag CDN services for country flag images
- YouTube video links provided through recipe data

---

## How It Works

1. **Recipe Retrieval**
   - The application requests recipe data from external APIs.
   - Recipe information is processed and displayed in a RecyclerView.

2. **Filtering & Search**
   - Users can search recipes by name.
   - Additional filters allow narrowing results by:
     - Country
     - Category
     - Preparation time
     - Ingredient count

3. **Recipe Exploration**
   - Selecting a recipe opens a detailed recipe screen.
   - Users can view ingredients, cooking instructions, images, and tutorial videos.

4. **Localization**
   - The application dynamically updates interface text when a different language is selected.

5. **Testing**
   - Espresso end-to-end tests validate key user workflows including searching, filtering, navigation, and recipe loading.

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/recipe-app.git
cd recipe-app
```

### 2. Open the Project

Open the project using:

```bash
Android Studio
```

### 3. Sync Dependencies

Allow Android Studio to automatically download and sync all Gradle dependencies.

### 4. Configure SDK

Ensure the project is configured with:

```bash
Compile SDK: 34
Target SDK: 34
Minimum SDK: 21
```

### 5. Build the Project

```bash
Build > Make Project
```

or

```bash
./gradlew build
```

### 6. Run the Application

Connect an Android device or start an emulator and run:

```bash
Run > Run 'app'
```

### 7. Execute UI Tests

```bash
connectedAndroidTest
```

or

```bash
./gradlew connectedAndroidTest
```

---

## Images

Below are screenshots of the main pages of the application.

### Home Screen

- Recipe browsing interface
- Search functionality
- Language selection

### Filter Dialog

- Country filtering
- Category filtering
- Time slider
- Ingredient count slider

### Recipe Results

- Filtered recipe list
- Country flags
- Category indicators

### Recipe Detail Screen

- Full recipe information
- Ingredients list
- Cooking instructions
- Recipe image

### Video Tutorial Section

- Embedded recipe video preview
- Quick access to cooking tutorials

---

## Testing

The project includes Espresso UI tests covering:

- Search functionality
- Filter dialog interaction
- Slider filtering
- Country filtering
- Category filtering
- Language switching
- RecyclerView population
- Recipe detail navigation
- Video section visibility
- Filter reset functionality

These tests help ensure that all major user workflows function correctly across Android devices.

---

## Future Enhancements

- Favourite recipes system
- User accounts and cloud synchronization
- Recipe recommendations based on preferences
- Offline recipe storage
- Nutritional information analysis
- Dark mode support
- Additional language translations
- Recipe rating and review functionality
- AI-powered recipe suggestions
- Meal planning and shopping list generation
