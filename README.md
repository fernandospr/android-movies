# Android Movies
Android Movies es una app Android desarrollada en Kotlin que utiliza la API de [The Movie Database](https://developers.themoviedb.org/3) para mostrar películas y series.

Main-Detail | Detail-Video | Search
:-: | :-: | :-:
<video src='https://user-images.githubusercontent.com/4404680/210565617-18a46b34-ba01-45b1-99e0-5cdba39cf14f.mp4' /> | <video src='https://user-images.githubusercontent.com/4404680/210566843-a6fc7617-ec5e-4002-abad-3f850f7fe623.mp4' /> | <video src='https://user-images.githubusercontent.com/4404680/210567484-cd59ca5f-3fa6-41c6-9e84-dc49d6e06f20.mp4' />

## Cómo correr la app
1. Clonar el repositorio.
2. Abrir en Android Studio.
3. (Opcional) Abrir `build.gradle` (app) y cambiar la API key que se puede obtener siguiendo los pasos de la [documentación de la API](https://developers.themoviedb.org/3/getting-started/introduction).
4. Correr la app en un dispositivo o emulador.

## Descripción
Utiliza las siguientes bibliotecas: 

* [Retrofit2](https://github.com/square/retrofit): Comunicación con la API.
* [Koin](https://github.com/InsertKoinIO/koin): Inyección de dependencias.
* [Glide](https://github.com/bumptech/glide): Carga de imágenes.
* [Room](https://developer.android.com/jetpack/androidx/releases/room): Guardado de datos de la API para que la app pueda funcionar offline.
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) + [LiveData](https://developer.android.com/topic/libraries/architecture/livedata): Los ViewModels obtienen el estado (películas/series) de los repositorios y los emiten a la UI utilizando LiveData.
* [RxJava](https://github.com/ReactiveX/RxJava): Utilizado en los repositorios para obtener datos de la API o base de datos en un hilo secundario y emitir los resultados en el hilo principal.
* [View Binding](https://developer.android.com/topic/libraries/view-binding): Para interactuar con las vistas más fácilmente.
* [JUnit](https://junit.org/junit4/) y [Mockito Kotlin](https://github.com/mockito/mockito-kotlin): Para crear tests unitarios.

El proyecto está organizado en paquetes por feature:
* `main`: Pantalla principal con listados de películas y series categorizados en Popular, Top Rated y Upcoming.
* `detail`: Pantalla de detalle de película/serie.
* `search`: Pantalla para buscar películas/series.

### Capas de Aplicación
`MoviesDatabase` y `MoviesDao` pertenecen a la capa de **Persistencia**. Estos se utilizan para el funcionamiento de la app offline. Tienen métodos para guardar/obtener datos de una base de datos.

`MainApi`, `DetailApi`, `SearchApi` y `Network` pertenecen a la capa de **Red**. Estos se utilizan para el funcionamiento de la app online. Consumen datos de la API.

`MainRepository`, `DetailRepository` y `SearchRepository` pertenecen a la capa de **Repositorio**, internamente consume de la capa de Persistencia y Red.

Las clases dentro del paquete `models` podrían pertenecer a la capa de **Negocio**. Tienen los datos que se muestran en la app. Dado que no tienen mucha lógica de negocio podría decirse que en realidad pertenecen a la capa de Red y/o de Persistencia.

Las clases `XXXXXActivity` pertenecen a la capa de **Vista**. Estas clases tienen solo lógica de setear texto, animaciones, imágenes y componentes de vista en general. Reciben la interacción del usuario.

Las clases `XXXXXAdapter` pertenecen a la capa de **Vista**. Se utilizan para mostrar items en un listado.

Se usó la arquitectura MVVM con `ViewModels` y `LiveData` de [Android Architecture Components](https://developer.android.com/topic/libraries/architecture), estos son el nexo entre Vista y Repositorio. Cuando el usuario interactúa con la vista, esta llama a un método del `ViewModel`, el cual consume del repositorio y setea los valores de los `LiveData`, estos últimos son observados por la Vista que actualiza de forma acorde.

Para inyectar dependencias se utilizó [Koin](https://insert-koin.io/), entonces en los paquetes `di` se pueden encontrar módulos que configuran las dependencias.

`MoviesApplication` representa a la app y es lo que primero se ejecuta. Inicializa Koin con los módulos.

### Principio de Responsabilidad Única
Cada clase debe tener una solo objetivo o problema a resolver. 

Por ejemplo, el `MainRepositoryImpl` tiene el objetivo de traer los datos, internamente obteniéndolos de una API o de una base de datos según si hay o no conexión a Internet. `MainRepositoryImpl` delega responsabilidades a:

* `NetworkImpl` para el chequeo de la conexión.
* La implementación de `MainApi` tiene el objetivo de traer los datos de la API.
* La implementación de `MoviesDao` tiene el objetivo de guardar/traer los datos de la base de datos.

Si todo esto estuviese implementado dentro de la misma clase, no se estaría cumpliendo este principio.

### Código Limpio
El proyecto fue desarrollado teniendo en cuenta estas características:

* Fácil lectura para otros desarrolladores.
* Fácil de extender o modificar.
* Clases pequeñas, con pocos métodos, pocas líneas. Publicar solo los métodos necesarios. El resto debería ser privado.
* Prestar atención al nombrado de clases, métodos y variables. Evitar nombres como "Manager", "Admin", etc.
* Dado que se presta atención a los nombres, no sería necesario escribir comentarios, a menos que:
	* Sean TODOs/FIXMEs.
	* Se utilicen bibliotecas externas que tienen malos nombres que no podemos modificar.
	* Sea necesario aclarar alguna decisión técnica o de negocio.
* Mantener formateo y consistencia entre clases. Si la clase A fue creada por el desarrollador A y la clase B fue creada por el desarrollador B, el desarrollador C al mirar ambas no debería poder identificar quien desarrolló qué clase.
* Debe tener tests unitarios que den confianza para que cualquier desarrollador pueda modificar clases sin temor a romper funcionalidad existente crítica.
* Inyectar dependencias: favoreciendo la responsabilidad única, alternativas de implementación y la facilidad de test.

[Robert C. Martin (Uncle Bob)](https://en.wikipedia.org/wiki/Robert_C._Martin) escribió el libro [***Clean Code: A Handbook of Agile Software Craftsmanship***](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship-ebook/dp/B001GSTOAM) sobre este tema.

## TODOs
* Agregar más tests unitarios. Solo se crearon los principales, para demostrar el uso de JUnit y Mockito. Los tests que faltan de ViewModels y Repositorios serían muy similares a los existentes.

* Obtener configuración primero antes de ejecutar el resto de los servicios. Esto serviría para agregar el base path a las imágenes. Ahora están hardcodeadas en la clase `Show`. Por ejemplo, se podría implementar usando RxJava y el operador [zip](http://reactivex.io/documentation/operators/zip.html), combinando el resultado de los observables de:
	* [Servicio de configuración](https://developers.themoviedb.org/3/configuration/get-api-configuration)
	* Servicio que devuelve películas/series.
       
* Manejar el caso de error cuando no carga la siguiente página.

* UI/UX
    * Se podría separar en dos secciones: Películas y Series. Por ejemplo, usando [BottomNavigationView](https://developer.android.com/reference/com/google/android/material/bottomnavigation/BottomNavigationView), aunque según las guías de [Material Design](https://m2.material.io/components/bottom-navigation) recomiendan que sean 3 o más destinos. Sino usar [tabs](https://m2.material.io/components/tabs).
    * Agregar más datos en el detalle. Por ejemplo, géneros.
    * Modificar [adaptive launcher icon](https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive).
    * Agregar [Splash screen](https://developer.android.com/develop/ui/views/launch/splash-screen).
    * Implementar [Light y Dark themes](https://developer.android.com/develop/ui/views/theming/darktheme).
    * Migrar a [Material3](https://material.io/blog/migrating-material-3).
