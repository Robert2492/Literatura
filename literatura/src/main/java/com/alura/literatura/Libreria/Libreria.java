package com.alura.literatura.Libreria;

import com.alura.literatura.Configuracion.ConsumoApiGutendex;
import com.alura.literatura.Configuracion.ConvertirDatos;
import com.alura.literatura.Modelos.Libro;
import com.alura.literatura.Modelos.Autor;
import com.alura.literatura.Modelos.LibrosAPI;
import com.alura.literatura.Modelos.Registros.DatosLibro;
import com.alura.literatura.Repositorios.iRepositorioAutor;
import com.alura.literatura.Repositorios.iRepositorioLibro;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class Libreria {

    private Scanner sc = new Scanner(System.in);
    private ConsumoApiGutendex consumoApi = new ConsumoApiGutendex();
    private ConvertirDatos convertir = new ConvertirDatos();
    private static String API_BASE = "https://gutendex.com/books/?search=";
    private iRepositorioLibro  repositorioLibro;
    private iRepositorioAutor repositorioAutor;

    public Libreria(iRepositorioLibro repositorioLibro, iRepositorioAutor repositorioAutor) {
        this.repositorioLibro = repositorioLibro;
        this.repositorioAutor = repositorioAutor;
    }






    public void consumo(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    
                    |----------------------------------------------------|
                    |------->    BIENVENIDO A LIBRERIA ALURA     <-------|
                   
                    
                    1 ---> Agregar Libro por Nombre
                    2 ---> Libros buscados
                    3 ---> Buscar libro por Nombre
                    4 ---> Buscar todos los Autores de libros buscados
                    5 ---> Buscar Autores por año
                    6 ---> Buscar Libros por Idioma
                    7 ---> Top 10 Libros mas Descargados
                    8 ---> Buscar Autor por Nombre        
              
                    0 - Salir
                   
                    |------------->  INGRESE UNA OPCIÓN  <---------------|
                 
                    """;

            try {
                System.out.println(menu);
                opcion = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("|---  Por favor, ingrese un número válido.  ---|\n");
                sc.nextLine();
                continue;
            }



            switch (opcion){
                case 1:
                    buscarLibroEnLaWeb();
                    break;
                case 2:
                    librosBuscados();
                    break;
                case 3:
                    buscarLibroPorNombre();
                    break;
                case 4:
                    BuscarAutores();
                    break;
                case 5:
                    buscarAutoresPorAnio();
                    break;
                case 6:
                    buscarLibrosPorIdioma();
                    break;
                case 7:
                    top10LibrosMasDescargados();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 0:
                    opcion = 0;

                    System.out.println("|    .... Cerrando Aplicación....   |\n");

                    break;
                default:

                    System.out.println("|  Opción Incorrecta. |");
                    System.out.println("|---------------------|\n");
                    System.out.println("Intente con una nueva Opción");
                    consumo();
                    break;
            }
        }
    }

    private Libro getDatosLibro(){
        System.out.println("Ingrese el nombre del libro: ");
        var nombreLibro = sc.nextLine().toLowerCase();
        var json = consumoApi.obtenerDatos(API_BASE + nombreLibro.replace(" ", "%20"));
        LibrosAPI datos = convertir.convertirDatosJsonAJava(json, LibrosAPI.class);

        if (datos != null && datos.getResultadoLibros() != null && !datos.getResultadoLibros().isEmpty()) {
            DatosLibro primerLibro = datos.getResultadoLibros().get(0); // Obtener el primer libro de la lista
            return new Libro(primerLibro);
        } else {
            System.out.println("No se encontraron resultados del libro.");
            return null;
        }
    }


    private void buscarLibroEnLaWeb() {
        Libro libro = getDatosLibro();

        if (libro == null){
            System.out.println("Libro no encontrado.");
            return;
        }

        try{
            boolean libroExists = repositorioLibro.existsByTitulo(libro.getTitulo());
            if (libroExists){
                System.out.println("El libro ya esta registrado!");
            }else {
                repositorioLibro.save(libro);
                System.out.println(libro.toString());
            }
        }catch (InvalidDataAccessApiUsageException e){
            System.out.println("No se puede persisitir el libro buscado!");
        }
    }

    @Transactional(readOnly = true)
    private void librosBuscados(){
        //datosLibro.forEach(System.out::println);
        List<Libro> libros = repositorioLibro.findAll();
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros");
        } else {
            System.out.println("Libros encontrados");
            for (Libro libro : libros) {
                System.out.println(libro.toString());
            }
        }
    }

    private void buscarLibroPorNombre() {
        System.out.println("Ingrese Titulo libro que quiere buscar: ");
        var titulo = sc.nextLine();
        Libro libroBuscado = repositorioLibro.findByTituloContainsIgnoreCase(titulo);
        if (libroBuscado != null) {
            System.out.println("El libro buscado fue: " + libroBuscado);
        } else {
            System.out.println("El libro con el titulo '" + titulo + "' no se encontró.");
        }
    }

    private  void BuscarAutores(){

        List<Autor> autores = repositorioAutor.findAll();

        if (autores.isEmpty()) {
            System.out.println("No se encontraron libros \n");
        } else {
            System.out.println("Libros encontrados \n");
            Set<String> autoresUnicos = new HashSet<>();
            for (Autor autor : autores) {
                if (autoresUnicos.add(autor.getNombre())){
                    System.out.println(autor.getNombre()+'\n');
                }
            }
        }
    }

    private void  buscarLibrosPorIdioma(){
        System.out.println("Ingrese Idioma en el que quiere buscar: \n");
        System.out.println("|-----------------------------------|");
        System.out.println("|  Opción - es : Libros en español. |");
        System.out.println("|  Opción - en : Libros en ingles.  |");
        System.out.println("|-----------------------------------|\n");

        var idioma = sc.nextLine();
        List<Libro> librosPorIdioma = repositorioLibro.findByIdioma(idioma);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros ");
        } else {
            System.out.println("Libros encontrados según su idioma");
            for (Libro libro : librosPorIdioma) {
                System.out.println(libro.toString());
            }
        }

    }

    private void buscarAutoresPorAnio() {

        System.out.println("Indica el año para consultar que autores estan vivos: \n");
        var anioBuscado = sc.nextInt();
        sc.nextLine();

        List<Autor> autoresVivos = repositorioAutor.findByCumpleaniosLessThanOrFechaFallecimientoGreaterThanEqual(anioBuscado, anioBuscado);

        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores que estuvieran vivos en el año " + anioBuscado + ".");
        } else {
            System.out.println("Los autores que estaban vivos en el año " + anioBuscado + " son:");
            Set<String> autoresUnicos = new HashSet<>();

            for (Autor autor : autoresVivos) {
                if (autor.getCumpleanios() != null && autor.getFechaFallecimiento() != null) {
                    if (autor.getCumpleanios() <= anioBuscado && autor.getFechaFallecimiento() >= anioBuscado) {
                        if (autoresUnicos.add(autor.getNombre())) {
                            System.out.println("Autor: " + autor.getNombre());
                        }
                    }
                }
            }
        }
    }

    private void top10LibrosMasDescargados(){
        List<Libro> top10Libros = repositorioLibro.findTop10ByTituloByCantidadDescargas();
        if (!top10Libros.isEmpty()){
            int index = 1;
            for (Libro libro: top10Libros){
                System.out.printf("Libro %d: %s Autor: %s Descargas: %d\n",
                        index, libro.getTitulo(), libro.getAutores().getNombre(), libro.getCantidadDescargas());
                index++;
            }

        }
    }


    private void buscarAutorPorNombre() {

        try {

            System.out.println("Ingrese nombre del escritor que quiere buscar: ");
            var escritor = sc.nextLine();
            Optional<Autor> escritorBuscado = repositorioAutor.findFirstByNombreContainsIgnoreCase(escritor);
            if (escritorBuscado != null) {
                System.out.println("\nEl escritor buscado fue: " + escritorBuscado.get().getNombre());

            } else {
                System.out.println("\nEl escritor con el titulo '" + escritor + "' no se encontró.");
            }

        }catch (Exception e){
            System.out.println("|---  Nombre del autor no encontrado.  ---|\n");
            consumo();

        }

    }
}
