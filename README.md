## 📱 Android Calendar Application

---

### 🧾 Description

<p>
 Calendar application for Android. Allows to create, edit and delete tasks. Daily tasks are generated for sport activities. Completing tasks give points. Points are used to unlock new decorations for the user avatar. Friends can add each other and compete in completing tasks. Balanced diet is generated for the user every day by defining main ingredient that must be consumed. Tips for healthy lifestyle are displayed every day.
</p>

### 🧑‍💻 Technology Stack

- Android Jetpack Compose
- Dagger Hilt
- Firebase
- Kotlin
- Material Design

---

### 🗃️ Project Structure

```yaml
    src
    └── main
        ├── java
        │   └── com
        │       └── pvp
        │           └── app
        │               ├── api               # Interfaces
        │               │   └── ...
        │               ├── common            # Common Use: Handlers, Utilities, ...
        │               │   └── ...
        │               ├── di                # Dependency Injection
        │               │   └── ...
        │               ├── model             # Data Models
        │               │   └── ...
        │               ├── service           # Services
        │               │   └── ...
        │               ├── ui
        │               │   ├── common        # Common Use: Components, Handlers, Utilities, ...
        │               │   │   └── ...
        │               │   ├── router        # Routing Configurations
        │               │   │   └── ...
        │               │   ├── screen        # Screens
        │               │   │   ├── calendar
        │               │   │   │   ├── CalendarScreen.kt
        │               │   │   │   ├── CalendarViewModel.kt
        │               │   │   │   └── ...
        │               │   │   └── ...
        │               │   └── theme         # Theme Configurations
        │               │       └── ...
        │               ├── worker            # Service Workers
        │               │   └── ...
        │               ├── Activity.kt
        │               └── Application.kt
        └── res
            └── ...                           # Resources
```

### 📎 Following The Structure

###### ◽ src/main/.../api
- Interfaces for various services are located in `api` package.
- If there is a need to define something with an interface, it should be located in `api` package.

###### ◽ src/main/.../common
- Common use classes that are not services, ui or models are located in `common` package.
- This is a place for utilities, handlers, extensions, etc.

###### ◽ src/main/.../di
- Binding modules for dependency injection are located in `di` package.
- Each module is located in a separate file.
- Prefer bindings over providing instances directly.

###### ◽ src/main/.../model
- Data models are located in `model` package.
- Each model is located in a grouped by its use file. i.e. `Task.kt` could hold `Task`
  and `TaskMeal` models.

###### ◽ src/main/.../service
- Service is a layer that controls most things within this application.
- Services handle database calls and business logic.
- UI > ViewModel > Service > Database

###### ◽ src/main/.../ui
- `Composable` functions (later on: `Components`) are used to build the UI.
- `Screen` is just a component that is used to build the UI out of other components.
- Screen components are located in `ui.screen` package.
- Each screen has its own `Screen` component and `ViewModel` if required.
- Components specific to a screen are located in the same package as the screen component.
- Components that are not specific to a screen are located in `ui.common` package, grouped by their
  use. i.e. `Texts.kt`, `Buttons.kt`, etc.
- `Router` is used to define routes to screens.

###### ◽ src/main/.../worker
- Service workers are located in `worker` package.
- Each worker is located in a separate file.
- Workers are used to perform background/foreground one-time/periodic tasks.

###### ◽ src/main/res
- Resources are located in `res` directory.
- `src/main/res/values/strings.xml` is used to define strings used in the application.

---

### ✒️ Code Style
- [Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- Remember to always
  - Remove any unnecessary comments, empty lines, imports and whitespace
  - Separate functions, classes, different parts of the code with a single empty line
    - Groups of the same type of code can stay together without an empty line
    - Multi-line code blocks should be separated by an empty line, even if they are of the same type
  - Use `val` instead of `var` whenever possible
  - Use `when` instead of `if` whenever possible, unless there are only 2 outcomes (use `if` in that case)
  - Use brackets for `if`, `for` and `while` blocks even if they are not required
    - Exception for `if`: conditionals that are in a single line
  - Use simple yet well defined names for variables, functions, classes, etc.

---