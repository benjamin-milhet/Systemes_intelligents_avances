import matplotlib.pyplot as plt

with open('FUN') as file:
    data = file.readlines()

f1_values = []
f2_values = []
for line in data:
    values = line.strip().split()
    f1_values.append(float(values[0]))
    f2_values.append(float(values[1]))

# Tracer le front de Pareto
plt.figure(figsize=(8, 6))
plt.scatter(f1_values, f2_values, color='blue', marker='o', label='Front de Pareto')
plt.title('Front de Pareto')
plt.xlabel('f1')
plt.ylabel('f2')
plt.legend()
plt.grid(True)
plt.show()