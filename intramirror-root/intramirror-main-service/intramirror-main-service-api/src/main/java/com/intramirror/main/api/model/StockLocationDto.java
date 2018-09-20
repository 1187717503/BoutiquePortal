package com.intramirror.main.api.model;

public class StockLocationDto extends StockLocation{

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
