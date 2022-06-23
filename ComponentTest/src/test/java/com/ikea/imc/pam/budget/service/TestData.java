package com.ikea.imc.pam.budget.service;

import java.util.concurrent.ThreadLocalRandom;

public class TestData {
    public Long projectId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
    public String NOTE = "my note";
}
