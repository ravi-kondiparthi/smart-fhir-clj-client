# Example App
Clojure pedestal service the hosts the redirect uri.

1. Start the application: `lein run`
1. In browser, hit Epic Sandbox Authorize endpoint: https://open-ic.epic.com/argonaut/oauth2/authorize?response_type=code&client_id=d23d75f4-7f77-49f3-809d-ef5ca0983e9b&redirect_uri=https://localhost:9307/epic/token/demo&scope=launch
1. log in username `fhirjason`, and password `epicepic1`
1. Select Jessica or Jason
1. Allow access
1. Ignore warning about insecure certs


Validate callback url in your browser: https://localhost:9307/epic/token/demo?code=IVON67JlVOr8Q6kgIqF3VL-wnL2P3g_qPLK5U98sqbIfHqbC2cJKKGjW89-3mY7-pFDVJs4EBteGScR4cuC5ksE8h2Op2MV6o9oCmqYmIqCA-zfmLetRsVPcY0YWkWRf&state=blah



