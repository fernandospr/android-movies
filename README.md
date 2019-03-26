## Capas de Aplicación
`MoviesDatabase` y `MoviesDao` pertenecen a la capa de **Persistencia**. Estos se utilizan para el funcionamiento de la app offline. Tienen métodos para guardar/obtener datos de una base de datos.

`MoviesApi` y `NetworkUtilsImpl` pertenecen a la capa de **Red**. Estos se utilizan para el funcionamiento de la app online. Consumen datos de la API.

`RepositoryImpl` pertenecen a la capa de **Repositorio**, internamente consumen de la capa de Persistencia y Red.

Las clases dentro del paquete `models` podrían pertenecer a la capa de **Negocio**. Tienen los datos que se muestran en la app. Dado que no tienen mucha lógica de negocio podría decirse que en realidad pertenecen a la capa de Red y/o de Persistencia.

Las clases `XXXXXActivity` pertenecen a la capa de **Vista**. Estas clases tienen solo lógica de setear texto, animaciones, imagenes y componentes de vista en general. Reciben la interacción del usuario.

Las clases `XXXXXAdapter` pertenecen a la capa de **Vista**. Se utilizan para mostrar items en un listado.

Se usó la arquitectura MVVM con `ViewModels` y `LiveData` de [Android Architecture Components](https://developer.android.com/topic/libraries/architecture), estos son el nexo entre Vista y Repositorio. Cuando el usuario interactúa con la vista, esta llama a método del `ViewModel`, el cual consumen del repositorio y setea los valores de los `LiveData`, estos últimos son observados por la Vista que actualiza de forma acorde.

Para inyectar dependencias se utilizó [Koin](https://insert-koin.io/), entonces en el paquete `di` se pueden encontrar módulos que configuran las dependencias.

`MoviesApplication` representa a la app y es lo que primero se ejecuta. Inicializa Koin con los módulos.

## Principio de Responsabilidad Única
Cada clase debe tener una solo objetivo o problema a resolver. 

Por ejemplo, el `RepositoryImpl` tiene el objetivo de traer los datos, internamente obteniéndolos de una API o de una base de datos según si hay o no conexión a Internet. `RepositoryImpl` delega la responsabilidades a:

* `NetworkUtilsImpl` para el chequeo de la conexión.

* La implementación de `MoviesApi` tiene el objetivo de traer los datos de la API.

* La implementación de `MoviesDao` tiene el objetivo de guardar/traer los datos de la base de datos.

Si todo esto estuviese implementado dentro de la misma clase, no se estaría cumpliendo el principio de responsabilidad única.

## Código Limpio
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

## TODOs en el proyecto
* Agregar más tests unitarios. Solo se crearon los principales, para demostrar el uso de JUnit y Mockito. Los tests que faltan de ViewModels serían muy similares a los existentes.

* Obtener configuración primero antes de ejecutar el resto de los servicios. Esto serviría para agregar el base path a las imagenes. Ahora están hardcodeadas en la clase `Show`. Por ejemplo, se podría implementar usando RxJava y el operador [zip](http://reactivex.io/documentation/operators/zip.html), combinando el resultado de los observables de:
       * [Servicio de configuración](https://developers.themoviedb.org/3/configuration/get-api-configuration)
       * Servicio que devuelve películas/series.
       
* Manejar los casos de error cuando no carga la siguiente página.

* UI/UX
	* Se podría separar en dos secciones: Películas y Series. Por ejemplo, usando [BottomNavigationView](https://developer.android.com/reference/android/support/design/widget/BottomNavigationView), aunque según las guías de [Material Design](https://material.io/design/components/bottom-navigation.html) recomiendan que sean 3 o más destinos. Sino usar [tabs](https://material.io/design/components/tabs.html).
	* Agregar más datos en el detalle. Por ejemplo, cantidad de estrellas.
	* Usar CardView para cada mostrar película/serie.
