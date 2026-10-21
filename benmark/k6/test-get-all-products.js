import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',

            rate: 1000,
            timeUnit: '1s',

            // Run for 30 seconds
            duration: '60s',

            // Start with 50 VUs
            preAllocatedVUs: 100,

            // Allow k6 to scale VUs when requests take longer
            maxVUs: 200,
        },
    },

    thresholds: {
        // Less than 1% HTTP errors
        http_req_failed: ['rate<0.01'],

        // Latency targets
        http_req_duration: [
            'p(90)<1000',
            'p(95)<2000',
            'p(99)<3000',
        ],
    },
};

export default function () {
    const url =
        'http://localhost:8080/api/v1/products' +
        '?page=0' +
        '&size=10' +
        '&sort=id' +
        '&direction=asc' +
        '&category=Accessories' +
        '&price=2990000';

    const res = http.get(url, {
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => r.body.includes('data'),
    });
}
