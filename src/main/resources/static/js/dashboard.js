/**
 * 
 */

 
document.addEventListener('DOMContentLoaded', (event) => {
    const userCtx = document.getElementById('userChart').getContext('2d');
    const salesCtx = document.getElementById('salesChart').getContext('2d');

    const userChart = new Chart(userCtx, {
		        
        type: 'bar',
        data: {
			
            labels: labels,
            datasets: [{
                label: '2024',
                data: data,
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });

    const salesChart = new Chart(salesCtx, {
        type: 'bar',
        data: {
            labels: ['January', 'February', 'March', 'April', 'May', 'June'],
            datasets: [{
                label: 'Sales',
                data: [5000, 10000, 7500, 12500, 15000, 20000],
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: 'rgba(153, 102, 255, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
});