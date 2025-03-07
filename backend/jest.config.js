module.exports = {
  preset: "ts-jest",
  testEnvironment: "node",
  collectCoverage: true,
  collectCoverageFrom: [
    "src/**/*.{ts,tsx}", // Collect coverage only from TypeScript files
    "!src/**/*.d.ts", // Exclude declaration files
  ],
  coverageDirectory: "coverage",
  transform: {
    "^.+\\.tsx?$": "ts-jest", // Transform TypeScript files with ts-jest
  },
  moduleFileExtensions: ["ts", "tsx", "js", "jsx", "json", "node"], // File extensions to handle
};