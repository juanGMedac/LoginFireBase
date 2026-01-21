
# LoginFireBase - Guía de Implementación

Este proyecto es un ejemplo práctico de cómo integrar **Firebase Authentication** en una aplicación Android nativa utilizando **Java**.

El objetivo es crear un sistema completo de registro e inicio de sesión con persistencia de datos (sesión mantenida) y diseño Material Design.

## Tecnologías Utilizadas
* **Lenguaje:** Java 11+
* **Entorno:** Android Studio (Ladybug o superior)
* **Backend:** Firebase Auth (Email & Password)
* **UI Pattern:** DataBinding
* **Diseño:** Material Design Components

---

## Paso 1: Configuración en Firebase Console

Para que la app funcione, necesita un "backend" en la nube.

1.  Ve a [Firebase Console](https://console.firebase.google.com/).
2.  Crea un nuevo proyecto llamado `LoginFireBase`.
3.  Registra la app Android con el paquete exacto: `es.medac.loginfirebase`.
4.  **Huella SHA-1:**
    * En Android Studio, abre la pestaña lateral derecha **Gradle**.
    * Navega a: `Tasks` -> `android` -> `signingReport`.
    * Copia el código SHA-1 y pégalo en la configuración de Firebase.
5.  Descarga el archivo `google-services.json` y muévelo a la carpeta `app/` de tu proyecto (Vista de Proyecto).

### ⚠️ Importante: Activar el servicio
En la consola de Firebase: **Authentication** -> **Método de acceso** -> **Correo electrónico/contraseña** -> **Habilitar** -> **Guardar**.

---

##  Paso 2: Dependencias (Gradle)

### `build.gradle (Project)`
Añadir el plugin de Google Services:
```groovy
plugins {
    id 'com.google.gms.google-services' version '4.4.2' apply false
}

```

### `build.gradle (Module: app)`

Configuración necesaria para Firebase y DataBinding:

```groovy
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services' // Plugin activo
}

android {
    // ...
    buildFeatures {
        dataBinding true // Activación de DataBinding
    }
}

dependencies {
    // Firebase BoM (Bill of Materials) para gestionar versiones
    implementation platform('com.google.firebase:firebase-bom:33.8.0')
    // Librería de autenticación
    implementation 'com.google.firebase:firebase-auth'
}

```

---

## Paso 3: Diseño de Interfaz (XML)

### Características Clave

* **Etiqueta `<layout>`:** Es obligatoria como raíz para usar DataBinding.
* **TextInputLayout:** Usamos `style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"` para el diseño moderno de las cajas de texto.
* **Iconos:** Se usan Vector Assets (`ic_email`, `ic_lock`) para evitar superposiciones de texto.

---

## Paso 4: Lógica (Java)

La lógica principal se encuentra en `MainActivity.java`.

### Puntos clave del código:

1. **Inicialización:**
```java
// Vinculación de vista
binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
// Instancia de Firebase
mAuth = FirebaseAuth.getInstance();

```


2. **Registro de Usuario:**
```java
mAuth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(this, task -> {
        if (task.isSuccessful()) {
            // Éxito: Navegar a Home
        } else {
            // Error: Mostrar mensaje
        }
    });

```


3. **Persistencia de Sesión (`onStart`):**
   Comprobamos si el usuario ya existe antes de cargar la vista para saltar el login.
```java
@Override
public void onStart() {
    super.onStart();
    if (mAuth.getCurrentUser() != null) {
        irAHome();
    }
}

```



---

## Paso 5: Cerrar Sesión (HomeActivity)

Para evitar que la app vuelva al login al pulsar "Atrás" sin haber cerrado sesión, o que vuelva a la Home después de cerrar sesión, gestionamos la pila de actividades:

```java
btnLogout.setOnClickListener(v -> {
    FirebaseAuth.getInstance().signOut(); // Cierra en servidor

    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
    // Banderas para limpiar el historial de navegación
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                    Intent.FLAG_ACTIVITY_NEW_TASK | 
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
});

```

---

## Solución de Errores Comunes

| Error | Causa Probable | Solución |
| --- | --- | --- |
| **"Cannot find symbol variable main"** | El código Java busca un ID `main` que no existe en el XML actual. | Revisa que los IDs en el `.java` coincidan con los del `.xml` (ej: `etEmail`, `btnRegister`). |
| **"Error: This operation is not allowed"** | El proveedor de correo no está activo en Firebase. | Ve a la consola -> Authentication -> Sign-in method y activa "Correo/Contraseña". |
| **App cierra al abrir ("Crash")** | Falta el archivo de configuración. | Asegúrate de que `google-services.json` está dentro de la carpeta `app`. |
| **Pantalla negra al salir** | Mala gestión de Intents. | Usa los `FLAGS` de limpieza de pila (ver Paso 5) al hacer Logout. |
| **Texto montado sobre el icono** | Uso de iconos antiguos. | Utiliza `Vector Assets` y asígnalos con `app:startIconDrawable`. |
