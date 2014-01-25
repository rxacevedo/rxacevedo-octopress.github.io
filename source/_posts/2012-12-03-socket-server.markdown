---
layout: post
title: "Multi-threaded Socket Server"
date: 2012-12-03 11:34
comments: true
categories: 
---

This a multi-threaded socket server/client system I put together for school using POSIX threads - it was also my C primer. I'm sure that there are much more efficient ways of doing this, but I figured I'd post it for anyone looking for an example to work off of (I make no guarantees regarding this code, I have much more to learn about multi-threading and C in general).

In the screenshot below, you can see 16 threads in queue while the main thread continues to handle requests, spawning new threads to serve each one (they exit too quickly to be seen in htop/the screenshot). I tested the server with some help from [my friend David](http://terite.com), who wrote a quick test case in Node to send a connection request every 5ms, writing the string "data" to the socket upon success.

![Multiple threads shown in htop](/images/posts/server_htop.png)

### Server program
{% include_code server/server.c %}

### Client program
{% include_code server/client.c %}

### Node test
{% include_code server/stress.js %}
