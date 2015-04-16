package com.flightapp.myapp.basespicemanager;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


public class ResourceSerializer implements JsonDeserializer<MinPriceResource.Resource> {

    @Override
    public MinPriceResource.Resource deserialize(JsonElement value, Type type,
                                JsonDeserializationContext context) throws JsonParseException {

        final JsonObject resourceJson = value.getAsJsonObject();

        final MinPriceResource.Resource resource = new MinPriceResource(). new Resource();
        resource.fare = resourceJson.get("fare").getAsInt();
        resource.typeoftravel = resourceJson.get("typeoftravel").getAsString();
        resource.vertical = resourceJson.get("vertical").getAsString();
        resource.roundtrip = resourceJson.get("roundtrip").getAsString();
        resource.destination = resourceJson.get("destination").getAsString();
        resource.returndate = resourceJson.get("returndate").getAsInt();
        resource.lastupdated = resourceJson.get("lastupdated").getAsString();
        resource.source = resourceJson.get("source").getAsString();
        resource.carrier = resourceJson.get("carrier").getAsString();
        resource.date = resourceJson.get("date").getAsLong();
        resource.clazz = resourceJson.get("class").getAsString();

        Type listType = new TypeToken<List<MinPriceResource.Resource.Extra>>(){}.getType();

        String extraStr = resourceJson.get("extra").getAsString();

        extraStr = extraStr.replaceAll("u(\"[^\"]+\")", "$1");

        JsonElement extraList = new JsonParser().parse(extraStr);

        resource.extra = context.deserialize(extraList, listType);

        return resource;
    }

}
