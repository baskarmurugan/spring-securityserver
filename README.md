Security Server
Getting started
This application is a stand-alone application which is used for authentication and authorization

Just by changing the database and entity it can be used as a security server for any application

As the calls are routed from other services, we have two custom methods to verify token 1.Authorization - verifyToken() 2.Authorization & Authentication - verifyTokenAuthorization() - (Need to pass original path from source)
