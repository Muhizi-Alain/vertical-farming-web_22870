import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import CropBatchesPage from './pages/CropBatchesPage'
import CropFamiliesPage from './pages/CropFamiliesPage'
import DashboardPage from './pages/DashboardPage'
import GrowersPage from './pages/GrowersPage'
import GrowTowersPage from './pages/GrowTowersPage'
import HarvestRecordsPage from './pages/HarvestRecordsPage'
import PlantingCyclesPage from './pages/PlantingCyclesPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<DashboardPage />} />
          <Route path="crop-families" element={<CropFamiliesPage />} />
          <Route path="grow-towers" element={<GrowTowersPage />} />
          <Route path="growers" element={<GrowersPage />} />
          <Route path="crop-batches" element={<CropBatchesPage />} />
          <Route path="planting-cycles" element={<PlantingCyclesPage />} />
          <Route path="harvest-records" element={<HarvestRecordsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
