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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringCacheController {

    private static final String cacheName = "sw-cache-name";

    @GetMapping("/get/{id}")
    @Cacheable(value = cacheName, key = "#id")
    public String getMethod(@PathVariable String id) {
        return String.format("get_%s", id);
    }

    @GetMapping("/put/{id}")
    @CachePut(value = cacheName, key = "#id")
    public String putMethod(@PathVariable String id) {
        return String.format("put_%s", id);
    }

    @GetMapping("/delete/{id}")
    @CacheEvict(value = cacheName, key = "#id")
    public String deleteMethod(@PathVariable String id) {
        return String.format("delete_%s", id);
    }
}
