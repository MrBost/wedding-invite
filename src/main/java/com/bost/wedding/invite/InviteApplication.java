package com.bost.wedding.invite;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
public class InviteApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(InviteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String  content= "✨ Love is in the Air (and the Pixels)! ✨ Oluwaseun \uD83D\uDC8D Opeyemi We’re tying the knot on December 20th, 2025, and your presence would mean the world to us. In celebration of love and care for our planet, we’re going digital to reduce waste and leave only memories, not footprints \uD83C\uDF3F. Click below to RSVP and share in our joy as we begin this beautiful journey together \uD83D\uDC9A https://wedding-invite-zxks.onrender.com/api/v1/wedding/invite/165e50d4bb9945449322bbf2c7711c35";
		System.out.println(formatWeddingMessage(content));

	}
	public static String formatWeddingMessage(String rawMessage) {
		if (rawMessage == null || rawMessage.isBlank()) {
			return "";
		}

		String text = rawMessage.trim().replaceAll("\\s+", " ");

		text = text
				.replace("✨ Love is in the Air (and the Pixels)! ✨", "✨ Love is in the Air (and the Pixels)! ✨\n")
				.replace("Oluwaseun 💍 Opeyemi", "Oluwaseun 💍 Opeyemi\n\n")
				.replace("We’re tying the knot on", "\nWe’re tying the knot on")
				.replace("In celebration of love and care for our planet,", "\n\nIn celebration of love and care for our planet,")
				.replace("Click below to RSVP", "\n\nClick below to RSVP");

		text = text.replaceAll("(https?://\\S+)", "\n$1");

		text = text.replaceAll("(\\n){3,}", "\n\n").trim();

		return text;
	}
}
