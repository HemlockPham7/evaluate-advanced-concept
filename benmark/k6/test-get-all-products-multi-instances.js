import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        // Target 1: Port 8080 (5000 req/s)
        port_8080: {
            executor: 'constant-arrival-rate',
            rate: 5000,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 250,
            maxVUs: 1000,
            exec: 'testPort8080',
        },
        // Target 2: Port 8180 (5000 req/s)
        port_8180: {
            executor: 'constant-arrival-rate',
            rate: 5000,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 250,
            maxVUs: 1000,
            exec: 'testPort8180',
        },
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: [
            'p(90)<1000',
            'p(95)<2000',
            'p(99)<3000',
        ],
    },
};

const commonParams = {
    headers: {
        'Content-Type': 'application/json',
    },
};

const queryParams = '?page=0&size=10&sort=id&direction=asc&category=Accessories&price=2990000';

export function testPort8080() {
    const res = http.get(`http://localhost:8180/api/v1/products${queryParams}`, commonParams);
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => r.body && r.body.includes('data'),
    });
}

export function testPort8180() {
    const res = http.get(`http://localhost:8180/api/v1/products${queryParams}`, commonParams);
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => r.body && r.body.includes('data'),
    });
}