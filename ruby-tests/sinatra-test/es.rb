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

require 'elasticsearch'
require 'skywalking'
require 'faraday'
require 'httpx/adapters/faraday'


Skywalking.start(disable_plugins: 'elasticsearch')

client = Elasticsearch::Client.new(hosts: ['http://localhost:9200'], log: true, transport_options: { request: { timeout: 5 } })


# 创建索引
def create_index(client)
  client.indices.create(index: 'test_index', body: { mappings: { properties: { name: { type: 'text' } } } })
  puts "Index created."
end

# 添加文档
def add_document(client, id, name)
  client.index(index: 'test_index', id: id, body: { name: name })
  puts "Document added: #{id} => #{name}"
end

# 查询文档
def get_document(client, id)
  response = client.get(index: 'test_index', id: id)
  puts "Document retrieved: #{response['_source']}"
rescue Elasticsearch::Transport::Transport::Errors::NotFound
  puts "Document not found."
end

# 更新文档
def update_document(client, id, name)
  client.update(index: 'test_index', id: id, body: { doc: { name: name } })
  puts "Document updated: #{id} => #{name}"
end

# 删除文档
def delete_document(client, id)
  client.delete(index: 'test_index', id: id)
  puts "Document deleted: #{id}"
end

# 执行 CRUD 操作
create_index(client)
add_document(client, '1', 'Document 1')
get_document(client, '1')
delete_document(client, '1')


sleep 30