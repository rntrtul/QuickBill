import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

const firebaseConfig = {
  apiKey: process.env.FIREBASE_API_KEY,
  authDomain: "quickbill-88a83.firebaseapp.com",
  projectId: "quickbill-88a83",
  storageBucket: "quickbill-88a83.appspot.com",
  messagingSenderId: "298965685857",
  appId: "1:298965685857:web:9e1354e29dccee346bbc20",
  measurementId: "G-894CMLDJZM",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

export default {
  db,
};
