package com.igarciamen.watchers.payloads.response;

public class ScrapeResultResponse {
    private int newItemsFound;
    private int updatedItemsFound;

    public ScrapeResultResponse(int newItemsFound, int updatedItemsFound) {
        this.newItemsFound = newItemsFound;
        this.updatedItemsFound = updatedItemsFound;
    }

    public int getNewItemsFound() { return newItemsFound; }
    public int getUpdatedItemsFound() { return updatedItemsFound; }
}