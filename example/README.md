# Example App
Clojure pedestal service the hosts the redirect uri.

1. Install `smart-on-fhir-clj-client` in your local maven repo: run `lein install` from parent directory
1. Start the application: `lein run`
1. In browser, load "https://localhost:9307", enter your client id and redirect uri and hit submit (alternatively, in your browser hit Epic Sandbox Authorize endpoint directly: `https://open-ic.epic.com/argonaut/oauth2/authorize?response_type=code&client_id={{client-id}}&redirect_uri=https://localhost:9307/epic/token/demo&scope=launch`)
1. log in username `fhirjason`, and password `epicepic1`
1. Select Jessica or Jason
1. Allow access
1. Ignore any warning about insecure (self-signed) certs 