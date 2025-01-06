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

public class AiRequestAnthropic extends AiRequestBase {

    private int maxTokens = 4096; // required, set by default to size of v3 models
    private String system;
    private List<Map<String, Object>> listOfFunctionObj; // tools
    //private List<Function> functions;

    // constructor
    public AiRequestAnthropic(AiRequest basicRequest) {
        super(basicRequest);
    }

    public String getSystem() {return system;}
    public void setSystem(String system) {this.system = system;}

    @JsonProperty("max_tokens")
    public int getMaxTokens() {return maxTokens;}
    public void setMaxTokens(int maxTokens) {this.maxTokens = maxTokens;}

    @JsonProperty("tools")
    public List<Map<String, Object>> getListOfFunctionObj() {return listOfFunctionObj;}
    public void setListOfFunctionObj(List<Map<String, Object>> listOfFunctionObj) {this.listOfFunctionObj = listOfFunctionObj;}


    @Override
    public String toString() {
        return "AiRequestAnthropic{" +
                "system='" + system + '\'' +
                ", listOfFunctionObj=" + listOfFunctionObj +
                ", maxTokens='" + maxTokens + '\'' +
                '}';
    }
}
