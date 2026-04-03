# Guía de Desarrollo

### 1. Componentes Shared
No creen botones o campos desde cero. Usen los de la carpeta `shared`:
- `ParkButton`: Botón azul (por defecto) o azul claro (`isSecondary = true`).
- `ParkTextField`: Para todos los inputs.
- `ParkHeader`: Cabecera con título y flecha atrás.

### 2. Cómo armar una pantalla de EasyPark
Para que todo encaje, usa este "esqueleto" en tu `Screen`:
```kotlin
Scaffold(
    topBar = {
        ParkHeader(
            title = "Título de la Pantalla",
            onBackClick = { navController.popBackStack() }, // Poner null si no quieres flecha
            onNotificationClick = { /* Acción */ } // Poner null si no quieres campana
        )
    },
    bottomBar = {
        // Elige el footer según el rol del usuario en tu pantalla
        DriverFooter(currentScreen = "home_driver", onNavigate = { /* nav */ })
    }
) { padding ->
    Column(modifier = Modifier.padding(padding)) {
        // TU CONTENIDO AQUÍ
    }
}
```
Nota sobre Iconos: En los footers he usado ColorFilter.tint(color). Esto significa que aunque tu imagen JPG sea negra o azul, el código la pintará automáticamente de Gris o Azul según si está seleccionada o no.

### 3. Navegación y Rutas
- Safe Args: Usamos `NavRoute` (data classes/objects) con `@Serializable`.
- Back Stack: Usen siempre `navController.popBackStack()` para volver atrás. Esto mantiene los datos de la pantalla anterior intactos.
- Insets: Apliquen siempre `navigationBarsPadding()` en los botones de la parte inferior para que el menú del celular no los tape.

### 4. Arquitectura y "Single Source of Truth"
- Repositorio Unificado: Todo lo relacionado a parqueos (Buscar, Detalle, Registrar, Reservar) vive en un único `ParkingRepository` dentro de shared. No dupliquen interfaces.
- Módulos de Koin:
  - `DataModule`: Repositorios e implementaciones Mock.
  - `DomainModule`: UseCases (uno por cada acción específica).
  - `PresentationModule`: ViewModels.
- Inyección con Parámetros: Si un ViewModel necesita un ID de navegación, usen la sintaxis manual en el módulo:
`viewModel { (id: String) -> MiViewModel(id, get()) }`

### 5. Recursos y Textos
- Cero Hardcoded Text: Todo texto estático debe estar en `strings.xml`.
- Iconos: Usar imágenes de `drawable` con `painterResource(Res.drawable.nombre)`.
- Colores: Usar las variables de `shared/ui` (`ParkBlue`, `ParkGray`, etc.) para que el modo oscuro sea más fácil de implementar después.

### 6. Datos (Mocks)
Los Mocks deben ser consistentes. Si creas un parqueo en el `Mock` de registro, asegúrate de que ese mismo ID devuelva datos coherentes en el `Mock` de detalles.
