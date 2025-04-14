package main

import (
	_ "github.com/apache/skywalking-go"
	"github.com/labstack/echo/v4"
)

func main() {
	e := echo.New()
	e.GET("/v1", func(c echo.Context) error {
		return c.JSON(200, "/v1")
	})
	e.Logger.Fatal(e.Start(":5001"))
}
