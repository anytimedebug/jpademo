package com.example.jpademo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JpademoApplication

fun main(args: Array<String>) {
	runApplication<JpademoApplication>(*args)
}
