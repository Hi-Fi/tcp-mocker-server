# Tcp mocker server
Mock server for testing to guard backend systems by returning cached response for same call(s).

## Connections
Server makes all connections at the TCP level. This might cause some issues especially with HTTP calls, where e.g. responses are not coming in single input stream back to mock. Currently system is tested mainly againts pure TCP connections (telnet, mainframe).

```
Caller <-> \<mockName\>IncomingGateway <-> \<mockName\>MockIncomingChannel -> Transformer (adds mockName to headers) -> ServiceChannel -> Service activator (handleRequestMessage) -> sendToBackend -gateway -> mockTargetTransformer -> fixHttpMessageHost -transformer (adds real endpoint host to HTTP messages) -> mockTargetRouter -> router -> \<mockName\>TargetOutgoingChannel -> \<mockName\>OutgoingGateway -> real system
                                                              |<------------Cached response returned from service activator-----------------------------------|                                                                                                                                                                                                                                        |                   
                                                              |<------------Response returned to the caller through gateway-----------------------------------|<----------------------------------------------------------------Response returned to service activator which caches it-----------------------------------------------------------------------------------------------------------------|                  
                                                              |<------------Response from specific mock service-----------------------------------------------|
```

\<mockName\> comes from mock configuration, and all elements containing that are dynamically created (either from properties or while adding mock runtime)

At the incoming gateway there's a serializer, that can split the call to multiple calls according settings.
  

## Configuration
Mocks can be configured either through application properties or at runtime. Runtime configuration is currently limited.

Application configuration keys are under mock.services as a list
```mock:
  services:
    - name: <name of the mock. Has to be unique. Mandatory>
      mockPort: <port that application created for traffic. Has to be free. Mandatory>
      targetHost: <IP or host to the actual endpoint (that's used if response is not cached). Mandatory if proxying to backend>
      targetPort: <Port to the actual endpoint (that's used if response is not cached). Mandatory if proxying to backend>
      mockBeanName: <Bean that generates the responses for the calls coming to specific mock port. Mandatory if generating responses in app.>
      endpointType: <HTTP or TCP. Not currently in use>
      messageStarter: <byte presentation of started bits (comma separated). Not hex>
      bytesToClear: <Integer Indices (comma separated) of bytes that needs to be set to 0.>
      fieldsToClear: <xml fields (comma separated) that needs to be cleared when calculating hash. E.g. timestamps and nonces>
      regexpFilters: <Additional regexp filters for SOAP calls. Format <regexp for replace>-><replacement>. E.g. UsernameToken-[a-fA-F0-9_-]*->UsernameToken-xxxx>
  ```

## Extending mock
There's 2 ways to extend the mock: 1 for how to parse the incoming message in a way, that dynamic data (e.g. timestamps) are not interfering with the hashing. Second possibility is to create Mockservices, that generate responses to calls.

### Parser extensions
Parser should implement interface `IPayloadParser`, and return the payload (as String) that is OK to be used for hash calculation. Bean name should be formed as <parser name>Parser (e.g. SoapParser in the `com.github.hi_fi.tcpMockeServer.parserscom.github.hi_fi.tcpMockeServer.parsers`).

### Mocking services
Service's default approach is to proxy the request to real backend, and next times respond directly with that cached content. Other option is to create class that implements `IMockService`, and configure the bean name to application configuration. That way the call is not sent to backend (unless that call is made in the implementing service). Request-response are still cached, so that generation might be done only once (note this if wanting to get e.g. latest timestamp with each call).

## Good to know
Only some basic UI pages are created. All start with service host and port (localhost:8080 by default)
- **/** - Information about running mocks
- **/cache** - List of cached responses and request hashes matching those
- **/add** - Possibility to add (simple) new mock runtime
- **/integration** - Spring integration statistics from system. Displays only one that are generated from properties
