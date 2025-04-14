// Licensed to Apache Software Foundation (ASF) under one or more contributor
// license agreements. See the NOTICE file distributed with
// this work for additional information regarding copyright
// ownership. Apache Software Foundation (ASF) licenses this file to you under
// the Apache License, Version 2.0 (the "License"); you may
// not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package main

import (
	"fmt"
	_ "github.com/apache/skywalking-go"
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"golang.org/x/sync/errgroup"
)

type ErrorGroup struct {
	errgroup.Group
}

func main() {
	r := gin.Default()
	r.GET("/mongo_get", mongoGet)
	r.Run(":8081")
}

func mongoGet(c *gin.Context) {
	var e ErrorGroup
	var m, _ = mongo.Connect(c, options.Client().ApplyURI("mongodb://127.0.0.1:27017"))
	filter := bson.D{{"name", "test"}}
	e.Go(func() (err error) {
		collection := m.Database("demo").Collection("testCollection")
		_, err = collection.Find(c, filter)
		if err != nil {
			return err
		}
		fmt.Println("first Find Done")
		_, err = collection.Find(c, filter)
		if err != nil {
			return err
		}
		fmt.Println("second Find Done")
		return nil
	})
	err := e.Wait()
	if err != nil {
		fmt.Println(err)
	}
}
