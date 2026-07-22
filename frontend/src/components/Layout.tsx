import {
  AppBar,
  Avatar,
  Badge,
  Box,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
} from "@mui/material";
import { useState } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import {
  FileUpload,
  Inventory2,
  Menu as MenuIcon,
  ReceiptLong,
  ShoppingCart,
  Logout,
} from "@mui/icons-material";
import keycloak, { login } from "../services/auth";
import { useCart } from "../features/cart/CartContext";
import { Footer } from './Footer';
export function Layout() {
  const { items } = useCart();
  const navigate = useNavigate();
  const location = useLocation();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const navItems = [
    { label: "Products", path: "/products", icon: <Inventory2 /> },
    { label: "Import CSV", path: "/products/import", icon: <FileUpload /> },
    { label: "Orders", path: "/orders", icon: <ReceiptLong /> },
    { label: "Cart", path: "/cart", icon: <ShoppingCart /> },
  ];
  const drawer = (
    <>
      <Toolbar sx={{ bgcolor: "primary.dark", color: "common.white" }}>
        <Typography variant="h6" fontWeight={700}>
          B2B Commerce
        </Typography>
      </Toolbar>
      <Divider />
      <List>
        {navItems.map((item) => {
          const selected = location.pathname.startsWith(item.path);
          return (
            <ListItem disablePadding key={item.path}>
              <ListItemButton
                selected={selected}
                onClick={() => {
                  navigate(item.path);
                  setDrawerOpen(false);
                }}
                sx={{
                  "&.Mui-selected": {
                    bgcolor: "rgba(21,101,192,.08)",
                    borderRight: "3px solid",
                    borderColor: "primary.main",
                  },
                }}
              >
                <ListItemIcon
                  sx={{ color: selected ? "primary.main" : "inherit" }}
                >
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.label}
                  slotProps={{
                    primary: {
                      fontWeight: selected ? 700 : 400,
                      color: selected ? "primary.main" : "inherit",
                    },
                  }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </>
  );
  return (
    <Box
      sx={{
        display: "flex",
        minHeight: "100vh",
        bgcolor: "background.default",
      }}
    >
      <AppBar
        position="fixed"
        color="inherit"
        sx={{
          width: { md: "calc(100% - 240px)" },
          ml: { md: "240px" },
          boxShadow: "0 1px 3px rgba(0,0,0,.1)",
        }}
      >
        <Toolbar>
          <IconButton
            onClick={() => setDrawerOpen(true)}
            sx={{ display: { md: "none" }, mr: 1 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography
            variant="h6"
            sx={{ display: { md: "none" }, flexGrow: 1 }}
          >
            B2B Commerce
          </Typography>
          <Box sx={{ flexGrow: 1, display: { xs: "none", md: "block" } }} />
          <IconButton
            onClick={() => navigate("/cart")}
            aria-label="Open cart"
          >
            <Badge badgeContent={items.length} color="secondary">
              <ShoppingCart />
            </Badge>
          </IconButton>
          {keycloak.authenticated ? (
            <>
              <IconButton onClick={(event) => setAnchorEl(event.currentTarget)}>
                <Avatar sx={{ width: 32, height: 32, bgcolor: "primary.main" }}>
                  U
                </Avatar>
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={() => setAnchorEl(null)}
              >
                <MenuItem disabled>Authenticated account</MenuItem>
                <Divider />
                <MenuItem
                  onClick={() =>
                    keycloak.logout({ redirectUri: window.location.origin })
                  }
                >
                  <ListItemIcon>
                    <Logout fontSize="small" />
                  </ListItemIcon>
                  Sign out
                </MenuItem>
              </Menu>
            </>
          ) : (
            <IconButton onClick={login}>
              <Avatar sx={{ width: 32, height: 32, bgcolor: "primary.main" }}>
                U
              </Avatar>
            </IconButton>
          )}
        </Toolbar>
      </AppBar>
      <Box component="nav" sx={{ width: { md: 240 }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="temporary"
          open={drawerOpen}
          onClose={() => setDrawerOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{ display: { md: "none" }, "& .MuiDrawer-paper": { width: 240 } }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          open
          sx={{
            display: { xs: "none", md: "block" },
            "& .MuiDrawer-paper": { width: 240 },
          }}
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { md: "calc(100% - 240px)" },
          mt: 8,
          p: { xs: 2, sm: 3 }, display: 'flex', flexDirection: 'column'
        }}
      >
        <Box sx={{ flexGrow: 1 }}><Outlet /></Box><Footer />
      </Box>
    </Box>
  );
}
