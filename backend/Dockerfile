# Use the official Node.js image as the base image
FROM node:18

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Copy the wait-for-it script
COPY wait-for-it.sh /app/wait-for-it.sh

# Build the TypeScript code
RUN npx tsc

# Expose the port the app runs on
EXPOSE 3000

# Command to run the application
CMD ["./wait-for-it.sh", "mysql:3306", "--", "node", "dist/index.js"]