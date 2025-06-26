package com.cooking.backend

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.lang.System.setProperty

@SpringBootApplication
class BackendApplication

fun main(args: Array<String>) {
//	val env = dotenv()
//
//	// Устанавливаем переменные в системные свойства, чтобы Spring их увидел
//	setProperty("DB_URL", env["DB_URL"])
//	setProperty("DB_USER", env["DB_USER"])
//	setProperty("DB_PASSWORD", env["DB_PASSWORD"])

	runApplication<BackendApplication>(*args)
}
