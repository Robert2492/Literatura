package com.alura.literatura;

import com.alura.literatura.Libreria.Libreria;
import com.alura.literatura.Repositorios.iRepositorioAutor;
import com.alura.literatura.Repositorios.iRepositorioLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraturaApplication implements CommandLineRunner {

	@Autowired
	private iRepositorioLibro repositorioLibro;
	@Autowired
	private iRepositorioAutor repositorioAutor;

	public static void main(String[] args) {

		SpringApplication.run(LiteraturaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Libreria libreria = new Libreria (repositorioLibro, repositorioAutor);
		libreria.consumo();
	}
}
