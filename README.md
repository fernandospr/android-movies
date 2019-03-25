## Capas de Aplicación
`MoviesDatabase` y `MovieDao` pertenecen a la capa de **Persistencia**. Estos se utilizan para el funcionamiento de la app offline.

`MoviesApi` y `NetworkUtilsImpl` pertenecen a la capa de **Red**. Estos se utilizan para el funcionamiento de la app online.

`RepositoryImpl` pertenecen a la capa de **Repositorio**, internamente consumen de la capa de Persistencia y Red.

Las clases dentro `Models` podrían pertenecer a la capa de **Negocio**. Aunque no tienen mucha lógica de negocio, así que podría decirse que en realidad pertenecen a la capa de Red y/o de Persistencia.

`Activities` y `Adapters` pertenecen a la capa de **Vista**. Estas clases tienen solo lógica de setear texto, animaciones, imagenes y componentes de vista en general. Reciben la interacción del usuario.

Se usó la arquitectura MVVM con `ViewModels` y `LiveData` de [Android Architecture Components (MVVM)](https://developer.android.com/topic/libraries/architecture), estos son el nexo entre Vista y Repositorio. Cuando el usuario interactúa con la vista, esta llama a método del `ViewModel`, el cual consumen del repositorio y setea los valores de los `LiveData`, estos últimos son observados por la Vista que actualiza de forma acorde.

Para inyectar dependencias se utilizó **Koin**, entonces en `AppModule` se configuran las dependencias.

`MoviesApplication` representa a la app y es lo que primero se ejecuta. Inicializa Koin con `AppModule`.

## Principio de Responsabilidad Única
Cada clase debe tener una solo objetivo o problema a resolver. 

Por ejemplo, el `RepositoryImpl` tiene el objetivo de traer los datos, internamente obteniéndolos de una API o de una base de datos según si hay o no conexión a Internet. `RepositoryImpl` delega la responsabilidades a:

* `NetworkUtilsImpl` para el chequeo de la conexión.

La implementación de `MoviesApi` tiene el objetivo de traer los datos de la API.

La implementación de `MoviesDao` tiene el objetivo de traer los datos de la base de datos.

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

Robert C. Martin (Uncle Bob) escribió el libro Clean Code sobre este tema.