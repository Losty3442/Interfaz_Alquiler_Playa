package com.playa.alquiler.model;

public class RecursoUso {
    private final int recursoId;
    private final long veces;

    public RecursoUso(int recursoId, long veces) {
        this.recursoId = recursoId;
        this.veces = veces;
    }

    public int getRecursoId() { return recursoId; }
    public long getVeces() { return veces; }
}