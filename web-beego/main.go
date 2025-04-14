package main

import (
	_ "github.com/apache/skywalking-go"
	beego "github.com/beego/beego/v2/server/web"
	"github.com/beego/beego/v2/server/web/context"
)

func main() {
	beego.Get("/status", func(ctx *context.Context) {
		ctx.Resp("hello world")
	})
	beego.Get("/t2", func(ctx *context.Context) {
		ctx.Resp("t2")
	})
	beego.Run("localhost:9000")
}
