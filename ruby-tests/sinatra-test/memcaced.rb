#  Licensed to the Apache Software Foundation (ASF) under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The ASF licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

require 'skywalking'
require 'dalli'

Skywalking.start

# 创建一个 Memcached 客户端
client = Dalli::Client.new('localhost:11211', { namespace: "my_app", compress: true })

# 设置一个键值对
client.set('my_key', 'Hello, Memcached!')

# # 获取值
value = client.get('my_key')
puts "The value for 'my_key' is: #{value}"
#
# # 更新值
# client.set('my_key', 'Updated Value!')
#
# # 获取更新后的值
# updated_value = client.get('my_key')
# puts "The updated value for 'my_key' is: #{updated_value}"
#
# # 删除一个键
# client.delete('my_key')
#
# # 尝试获取已删除的值
# value_after_delete = client.get('my_key')
# puts "The value for 'my_key' after deletion is: #{value_after_delete.nil? ? 'nil' : value_after_delete}"

sleep 20