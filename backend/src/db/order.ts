import { db } from "../firebase";
import { UserOrder, FirebaseOrder } from "../types";
import { doc, updateDoc, arrayUnion, getDoc, setDoc } from "firebase/firestore";

const addUserToOrder = async (orderId: string, userId: string) => {
  try {
    const orderSnap = await getDoc(doc(db, "orders", orderId));

    if (orderSnap.exists()) {
      await updateDoc(orderSnap.ref, {
        userIds: arrayUnion(userId),
      });
    } else {
      const firebaseOrder: FirebaseOrder = { userIds: [userId] };
      await setDoc(doc(db, "orders", orderId), firebaseOrder);
    }
  } catch (e) {
    console.log(e);
    throw e;
  }
};

const addUserOrderToOrder = async (orderId: string, userOrder: UserOrder) => {
  try {
    const orderSnap = await getDoc(doc(db, "orders", orderId));

    if (orderSnap.exists()) {
      await updateDoc(orderSnap.ref, {
        userOrders: arrayUnion(userOrder),
      });
    } else {
      const firebaseOrder: FirebaseOrder = { userOrders: [userOrder] };
      await setDoc(doc(db, "orders", orderId), firebaseOrder);
    }
  } catch (e) {
    console.log(e);
    throw e;
  }
};

const getUserOrdersByOrderId = async (orderId: string) => {
  try {
    const orderSnap = await getDoc(doc(db, "orders", orderId));

    if (orderSnap.exists()) {
      return orderSnap.data().userOrders;
    } else {
      return [];
    }
  } catch (e) {
    console.log(e);
    throw e;
  }
};

const getUsersByOrderId = async (orderId: string) => {
  try {
    const orderSnap = await getDoc(doc(db, "orders", orderId));

    if (orderSnap.exists()) {
      return orderSnap.data().userIds;
    } else {
      return [];
    }
  } catch (e) {
    console.log(e);
    throw e;
  }
};

export default {
  addUserOrderToOrder,
  getUserOrdersByOrderId,
  addUserToOrder,
  getUsersByOrderId,
};
