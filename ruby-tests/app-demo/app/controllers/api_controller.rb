require 'json'

class ApiController < ApplicationController
  def redis
    render json: { message: "hello redis" }
  end
end
