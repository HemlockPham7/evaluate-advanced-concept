import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        // Target 1: Port 9090 (5000 req/s)
        port_9090: {
            executor: 'constant-arrival-rate',
            rate: 5000,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 250,
            maxVUs: 1000,
            exec: 'testPort9090',
        },
        // Target 2: Port 9190 (5000 req/s)
        port_9190: {
            executor: 'constant-arrival-rate',
            rate: 5000,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 250,
            maxVUs: 1000,
            exec: 'testPort9190',
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

export function testPort9090() {
    const res = http.get(`http://localhost:9090/api/v1/products${queryParams}`, commonParams);
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => r.body && r.body.includes('data'),
    });
}

export function testPort9190() {
    const res = http.get(`http://localhost:9190/api/v1/products${queryParams}`, commonParams);
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => r.body && r.body.includes('data'),
    });
}