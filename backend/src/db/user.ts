import { db } from "../firebase";
import { User } from "../types";
import { doc, updateDoc, getDoc, setDoc } from "firebase/firestore";

const updateFirebaseMessagingToken = async (userId: string, firebaseMessagingToken: string) => {
  try {
    const userSnap = await getDoc(doc(db, "users", userId));

    if (userSnap.exists()) {
      await updateDoc(userSnap.ref, {
        firebaseMessagingToken: firebaseMessagingToken,
      });
    } else {
      const user: User = { firebaseMessagingToken };
      await setDoc(doc(db, "users", userId), user);
    }
  } catch (e) {
    console.log(e);
    throw e;
  }
};

const getUserById = async (userId: string) => {
  try {
    const userSnap = await getDoc(doc(db, "users", userId));

    if (userSnap.exists()) {
      return userSnap.data();
    } else {
      return {};
    }
  } catch (e) {
    console.log(e);
    throw e;
  }
};

export default {
  updateFirebaseMessagingToken,
  getUserById,
};
