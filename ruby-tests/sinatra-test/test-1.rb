module Dapper
  def brave
    "Hello from brave"
  end
end

class User
  prepend Dapper
end

p User.new.brave # => "Hello from brave"