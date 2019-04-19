package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.TransactionDTO;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bastronaut.bigspender.enums.TransactionCode.BA;
import static com.bastronaut.bigspender.enums.TransactionCode.GT;
import static com.bastronaut.bigspender.enums.TransactionMutationType.BETAALAUTOMAAT;
import static com.bastronaut.bigspender.enums.TransactionMutationType.ONLINEBANKIEREN;
import static com.bastronaut.bigspender.enums.TransactionType.AF;
import static com.bastronaut.bigspender.enums.TransactionType.BIJ;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping(value = "/{userid}/transactions",  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getTransaction(@PathVariable int userid, @AuthenticationPrincipal User user) {
//        return "returning " + Integer.toString(userid);


        final Transaction t1 = new Transaction(LocalDate.of(2019, 04, 01),
                LocalTime.of(22,39), "AH to go 5869 DenHaa", "NL41INGB0006212385",
                null, GT, AF, 180, BETAALAUTOMAAT,
                "Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019", user);

        final Transaction t2 = new Transaction(LocalDate.of(2019, 04, 02),
                LocalTime.of(02,39), "AH to go 5869 DenHaa", "NL41INGB0006451386",
                null, BA, BIJ, 1180, BETAALAUTOMAAT,
                "Pasvolgnr: 008 01-04-2019 02:39 Valutadatum: 02-04-2019", user);

        final Transaction t3 = new Transaction(LocalDate.of(2019, 04, 03),
                LocalTime.of(14,15), "AH to go", "NL20INGB0001234567",
                "NL20INGB0007654321", null, AF, 1980, ONLINEBANKIEREN,
                "Pasvolgnr: 008 01-04-2019 14:15 Valutadatum: 02-04-2019", user);

        List txs = Arrays.asList(t1, t2, t3);


        List<Transaction> txsSaved = transactionRepository.saveAll(txs);
        List<TransactionDTO> result = txsSaved.stream().map(TransactionDTO::new).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
