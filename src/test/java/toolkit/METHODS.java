package toolkit;

import org.apache.http.client.methods.*;

import java.net.URI;

public enum METHODS {

    GET {
        @Override
        public HttpRequestBase getMethod(URI uri) {
            return new HttpGet(uri);
        }
    }, POST {
        @Override
        public HttpRequestBase getMethod(URI uri) {
            return new HttpPost(uri);
        }
    }, PUT {
        @Override
        public HttpRequestBase getMethod(URI uri) {
            return new HttpPut(uri);
        }
    }, DELETE {
        @Override
        public HttpRequestBase getMethod(URI uri) {
            return new HttpDelete(uri);
        }
    };

    public abstract HttpRequestBase getMethod(URI uri);

}