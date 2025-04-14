package main

import (
	_ "github.com/apache/skywalking-go"
	"github.com/kataras/iris/v12"
)

func main() {
	app := iris.New()
	app.Use(iris.Compression)

	app.Get("/", func(ctx iris.Context) {
		ctx.HTML("Hello <strong>%s</strong>!", "World")
	})

	app.Listen(":7001")
}
