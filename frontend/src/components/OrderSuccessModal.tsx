import { CheckCircle } from "@mui/icons-material";
import { Dialog, DialogActions, DialogContent, DialogTitle, Button } from "@mui/material";
export function OrderSuccessModal({ orderId, onOrders, onContinue }: { orderId: number | null; onOrders: () => void; onContinue: () => void }) {
  return <Dialog open={orderId !== null} onClose={onContinue}><DialogTitle className="text-center"><CheckCircle color="success" sx={{ fontSize: 54 }} /><br />Order placed successfully!</DialogTitle><DialogContent>Your order #{orderId} was created and is being processed.</DialogContent><DialogActions><Button onClick={onContinue}>Continue shopping</Button><Button variant="contained" onClick={onOrders}>View orders</Button></DialogActions></Dialog>;
}
