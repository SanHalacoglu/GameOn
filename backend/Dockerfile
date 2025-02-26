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

# Copy the wait-for-it script
COPY wait-for-it.sh /app/wait-for-it.sh

# Build the TypeScript code
RUN npx tsc

# Expose the port the app runs on
EXPOSE $PORT

# Command to run the application
# This `sh -c ...` structure allows two separate commands to be run in a row
# The first command `sed ... wait-for-it.sh;` removes all carriage returns if the file is copied from a Windows machine
CMD ["sh", "-c", "sed -i 's/\r$//' wait-for-it.sh; ./wait-for-it.sh mysql:3306 -- node dist/index.js"]