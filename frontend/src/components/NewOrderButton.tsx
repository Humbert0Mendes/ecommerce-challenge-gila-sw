import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

export function NewOrderButton() {
  const navigate = useNavigate();
  return (
    <button className="btn btn-primary" onClick={() => navigate("/cart")}>
      <Add fontSize="small" aria-hidden="true" />
      New Order
    </button>
  );
}
