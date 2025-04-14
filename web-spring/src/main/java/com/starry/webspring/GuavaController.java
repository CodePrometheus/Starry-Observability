/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.starry.webspring;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuavaController {

    @GetMapping("/t2")
    public void t2() throws ExecutionException {
        Cache<String, Object> cache = CacheBuilder.newBuilder().softValues().build();
        Map<String, String> data = new HashMap<>();
        data.put("2", "value-2");

        // put operations
        cache.put("1", "value-1");
        cache.putAll(data);

        // get operations
        cache.get("1", () -> "default");
        cache.getIfPresent("1");
        cache.getAllPresent(data.keySet());

        // delete operations
        cache.invalidate("1");
        cache.invalidateAll(data.keySet());
        cache.invalidateAll();
    }
}
