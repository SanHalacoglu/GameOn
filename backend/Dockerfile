# Use the official Node.js image as the base image
FROM node:18

# Environment Variables
ARG PORT

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Expose the port the app runs on
EXPOSE $PORT

# Command to run the application
CMD ["npm", "start"]