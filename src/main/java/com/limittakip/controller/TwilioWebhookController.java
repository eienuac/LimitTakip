package com.limittakip.controller;

import com.limittakip.service.ExpenseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Twilio WhatsApp webhook isteklerini karsilayan Controller.
 * Twilio, gelen mesajlari bu endpoint'e POST olarak iletir.
 */
@RestController
@RequestMapping("/api/webhook")
public class TwilioWebhookController {

    private final ExpenseService expenseService;

    public TwilioWebhookController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Twilio'nun WhatsApp webhook POST istegini karsilar.
     * Gelen mesaj "Body" parametresinde tasinir.
     * Yanit, TwiML (XML) formatinda donulur.
     *
     * @param body Twilio'dan gelen mesaj icerigi
     * @return TwiML formatinda yanit
     */
    @PostMapping
    public ResponseEntity<String> handleIncomingMessage(@RequestParam("Body") String body) {

        String yanit = expenseService.mesajIsle(body);

        // Gecersiz mesajda bos TwiML don (hicbir islem yapma)
        if (yanit == null) {
            String bosTwiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>";
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(bosTwiml);
        }

        // Basarili islemde TwiML ile yanit don
        String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Response><Message>" + yanit + "</Message></Response>";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(twiml);
    }
}
