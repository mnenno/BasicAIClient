package it.nenno.basicaiclient.v1.config;

public class ProviderModel {
    private String provider;
    private String modelName;

    public ProviderModel(String provider, String modelName) {
        this.provider = provider;
        this.modelName = modelName;
    }

    public String getProvider() {
        return provider;
    }

    public String getModelName() {
        return modelName;
    }

    @Override
    public String toString() {
        return provider + "/" + modelName;
    }
}

