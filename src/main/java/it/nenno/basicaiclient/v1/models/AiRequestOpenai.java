/*
 * Copyright 2025 Mario Nenno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nenno.basicaiclient.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class AiRequestOpenai extends AiRequestBase {

    // List of nested map-structure
    @JsonProperty("tools")
    private List<Map<String, Object>> listOfFunctionObj; // functions
    private Object responseFormat;

    // constructor
    public AiRequestOpenai(AiRequest basicRequest) {
        super(basicRequest);
    }


    // getters and setters
    @JsonProperty("tools")
    public List<Map<String, Object>> getListOfFunctionObj() {return listOfFunctionObj;}
    public void setListOfFunctionObj(List<Map<String, Object>> listOfFunctionObj) {this.listOfFunctionObj = listOfFunctionObj;}

    @JsonProperty("response_format")
    public Object getResponseFormat() {return responseFormat;}
    public void setResponseFormat(Object responseFormat) {this.responseFormat = responseFormat;}


    @Override
    public String toString() {
        return "AiRequestOpenai{" +
                super.toString() + ", " +
                "listOfFunctionObj=" + listOfFunctionObj +
                ", responseFormat=" + responseFormat +
                '}';
    }
}
